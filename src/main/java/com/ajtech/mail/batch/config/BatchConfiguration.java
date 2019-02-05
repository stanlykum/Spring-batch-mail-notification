package com.ajtech.mail.batch.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.ajtech.mail.batch.domain.Customer;
import com.ajtech.mail.batch.enums.ApplicationConstants;
import com.ajtech.mail.batch.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

@Configuration
public class BatchConfiguration {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

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
	public JsonFileItemWriter<Customer> writer1() throws JsonProcessingException {
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
	public StaxEventItemWriter<Customer> writer() {
		StaxEventItemWriter<Customer> writer = new StaxEventItemWriter<Customer>();
		writer.setRootTagName("Customers");
		writer.setResource(new FileSystemResource("xml/customer.xml"));
		writer.setMarshaller(marshaller());
		return writer;
	}

	private XStreamMarshaller marshaller() {
		XStreamMarshaller marshaller = new XStreamMarshaller();
		@SuppressWarnings("rawtypes")
		Map<String, Class> map = new HashMap<>();
		map.put("Customer", Customer.class);
		marshaller.setAliases(map);
		return marshaller;
	}

	@Bean
	public Step step1() throws JsonProcessingException {
		return stepBuilderFactory.get("step1").<Customer, Customer>chunk(10).reader(customerFlatFileItemReader())
				.processor(customerProcess()).writer(writer1()).build();
	}

	@Bean
	public Job runJob() throws JsonProcessingException {
		return jobBuilderFactory.get("report generation").flow(step1()).end().build();
	}

	public ItemProcessor<Customer, Customer> customerProcess() {
		ItemProcessor<Customer, Customer> process = new ItemProcessor<Customer, Customer>() {
			@Override
			public Customer process(Customer item) throws Exception {
				return item;
			}
		};
		return process;

	}

}