package com.cts.batch.config;

import java.beans.PropertyEditor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonObjectMarshaller;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.cts.batch.config.FeignConfiguration.TokenFeignClient;
import com.cts.batch.domain.Customer;
import com.cts.batch.enums.ApplicationConstants;
import com.cts.batch.listener.JobCompletionNotificationListener;
import com.cts.batch.service.S3Service;
import com.cts.batch.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class BatchConfiguration {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private TokenFeignClient tokenFeignClient;

	@Bean
	public FlatFileItemReader<Customer> customerFlatFileItemReader() {
		FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new ClassPathResource("inputfile.txt"));
		flatFileItemReader.setLineMapper(createCustomerLineMapper());
		flatFileItemReader.setStrict(false);
		return flatFileItemReader;

	}

	@Bean
	public LineMapper<Customer> createCustomerLineMapper() {
		DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();

		BeanWrapperFieldSetMapper<Customer> customerBeanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<Customer>();
		customerBeanWrapperFieldSetMapper.setTargetType(Customer.class);

		DateFormat df = new SimpleDateFormat("ddmmyyyy");
		@SuppressWarnings("rawtypes")
		Map<Class, PropertyEditor> customEditors = Stream
				.of(new AbstractMap.SimpleEntry<>(Date.class, new CustomDateEditor(df, false)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		customerBeanWrapperFieldSetMapper.setCustomEditors(customEditors);

		customerLineMapper.setLineTokenizer(customerLineTokenizer());
		customerLineMapper.setFieldSetMapper(customerBeanWrapperFieldSetMapper);

		return customerLineMapper;
	}

	@Bean
	public LineTokenizer customerLineTokenizer() {
		FixedLengthTokenizer fixedLengthTokenizer = new FixedLengthTokenizer();
		Range[] ranges = new Range[] {
				new Range(ApplicationConstants.ACT_NBR_START_POSITION.getValue(),
						ApplicationConstants.ACT_NBR_END_POSITION.getValue()),
				new Range(ApplicationConstants.BANK_ACT_NBR_START_POSITION.getValue(),
						ApplicationConstants.BANK_ACT_NBR_END_POSITION.getValue()),
				new Range(ApplicationConstants.CARD_MEMBER_NAME_START_POSITION.getValue(),
						ApplicationConstants.CARD_MEMBER_NAME_END_POSITION.getValue()),
				new Range(ApplicationConstants.PAYMENT_DUE_DATE_START_POSITION.getValue(),
						ApplicationConstants.PAYMENT_DUE_DATE_END_POSITION.getValue()), };
		fixedLengthTokenizer.setColumns(ranges);
		fixedLengthTokenizer.setNames(new String[] { "actNbr", "bankActNbr", "cardMemberName", "paymentDueDate" });
		return fixedLengthTokenizer;
	}

	@Bean
	public JsonFileItemWriter<Customer> writer() throws JsonProcessingException {
		JsonObjectMarshaller<Customer> jsonObjectMarshaller = object -> {
			return populateCustomers(object);
		};

		JsonFileItemWriter<Customer> jsonFileItemWriter = new JsonFileItemWriter<>(
				new FileSystemResource("xml/customer.json"), jsonObjectMarshaller);
		return jsonFileItemWriter;
	}

	private String populateCustomers(Customer object) {
		try {
			if (object instanceof Customer) {
				return JsonUtils.convertObjectToJsonString(object);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Bean
	public Step step1() throws JsonProcessingException {
		return stepBuilderFactory.get("step1").<Customer, Customer>chunk(10).reader(customerFlatFileItemReader())
				.processor(customerProcess()).writer(writer()).build();
	}

	@Bean
	public Job runJob() throws JsonProcessingException {
		return jobBuilderFactory.get("report generation").listener(joblistener()).flow(step1()).end().build();
	}

	public ItemProcessor<Customer, Customer> customerProcess() {
		ItemProcessor<Customer, Customer> process = new ItemProcessor<Customer, Customer>() {
			@Override
			public Customer process(Customer item) throws Exception {
				log.info("Actual Account={},Token from feign={}",item.getActNbr(),tokenFeignClient.retrieveTokenByCardNo(item.getActNbr()));
				
				//DocumentContext jsonContext = JsonPath.parse(tokenFeignClient.retrieveTokenByCardNo(item.getActNbr()));
				//String jsonpathCreatorName = jsonContext.read(".$[''tokenNo']");
				//item.setActNbr(tokenFeignClient.retrieveTokenByCardNo(item.getActNbr()));
				return item;
			}
		};
		return process;

	}
	
	
	@Bean
	public JobCompletionNotificationListener joblistener() {
		return new JobCompletionNotificationListener();
	}


}