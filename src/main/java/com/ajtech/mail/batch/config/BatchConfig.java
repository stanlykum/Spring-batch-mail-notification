/*package com.ajtech.mail.batch.config;

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
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework .batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.support.DefaultPropertyEditorRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.ajtech.mail.batch.domain.Person;
import com.ajtech.mail.batch.util.MailUtil;

@Configuration
public class BatchConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private MailUtil util;

	@Bean
	public CustomDateEditor dateEditor() {
		return new CustomDateEditor(new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy"), true);

	}

	@Bean
	public FlatFileItemReader<Person> reader() {
		FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("sample-data.csv"));
		reader.setLinesToSkip(1);

		DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames("custId", "name", "email", "contactNo", "dob", "status", "outstandingAmount", "lastDueDate");

		BeanWrapperFieldSetMapper<Person> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Person.class);

		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		@SuppressWarnings("rawtypes")
		Map<Class, PropertyEditor> customEditors = Stream
				.of(new AbstractMap.SimpleEntry<>(Date.class, new CustomDateEditor(df, false)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		fieldSetMapper.setCustomEditors(customEditors);

		lineMapper.setFieldSetMapper(fieldSetMapper);
		lineMapper.setLineTokenizer(tokenizer);
		reader.setLineMapper(lineMapper);

		return reader;
	}

	@Bean
	public StaxEventItemWriter<Person> writter() {
		StaxEventItemWriter<Person> writter = new StaxEventItemWriter<Person>();
		writter.setRootTagName("Persons");
		writter.setResource(new FileSystemResource("xml/bank.xml"));
		writter.setMarshaller(marshaller());
		return writter;
	}

	private XStreamMarshaller marshaller() {
		XStreamMarshaller marshaller = new XStreamMarshaller();
		@SuppressWarnings("rawtypes")
		Map<String, Class> map = new HashMap<>();
		map.put("Person", Person.class);
		marshaller.setAliases(map);
		return marshaller;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Person, Person>chunk(10).reader(reader()).processor(process())
				.writer(writter()).build();
	}

	@Bean
	public Job runJob() {
		return jobBuilderFactory.get("report generation").flow(step1()).end().build();
	}

	public ItemProcessor<Person, Person> process() {

		ItemProcessor<Person, Person> process = new ItemProcessor<Person, Person>() {

			@Override
			public Person process(Person person) throws Exception {
				if (person.getStatus().equalsIgnoreCase("Pending")) {
					String msg = util.sendEmail(person.getEmail(), buildMessage(person));
					System.out.println(msg);
					return person;
				}
				return null;
			}

			private String buildMessage(Person person) {
				String mailBody = "Dear " + person.getName() + "," + "\n" + "statement of your credit card ending with "
						+ person.hashCode() + "**" + " has been generated" + "\n" + "dues amount :"
						+ person.getOutstandingAmount() + "\n" + "last payment date : "
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a").format(person.getLastDueDate()) + "\n" + "\n"
						+ "If you already paid please ignore this email" + "\n" + "Thanks for using our credit card ";
				return mailBody;
			}

		};
		return process;

	}

}
*/