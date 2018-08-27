package com.ef;

import com.ef.model.TimeDuration;
import com.ef.components.LogRecordCSVParser;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@SpringBootApplication
public class Parser {

	private static LogRecordCSVParser logRecordCSVParser;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Parser.class);
		app.setBannerMode(Banner.Mode.OFF);
		ConfigurableApplicationContext context = app.run(args);

		logRecordCSVParser = context.getBean(LogRecordCSVParser.class);
		try
		{
			Options options = new Options();
			Option startDate = new Option("s", "startDate", true, "Start date for eval, in ISO8601 Format");
			startDate.setRequired(true);
			options.addOption(startDate);
			Option duration = new Option("d","duration", true, "Duration - hourly, daily supported");
			duration.setRequired(true);
			options.addOption(duration);
			Option threshold = new Option("t", "threshold", true, "Threshold limit of IP occurences within state duration.");
			threshold.setRequired(true);
			options.addOption(threshold);
			Option accesslog = new Option("l", "accesslog", true, "Path to csv file for loading. Will use default access.log if not provided.");
			accesslog.setRequired(false);
			options.addOption(accesslog);

			CommandLineParser parser = new DefaultParser();
			HelpFormatter formatter = new HelpFormatter();
			CommandLine commandLine;
			try
			{
				commandLine = parser.parse(options, args);
				String startDateArg = commandLine.getOptionValue("startDate");
				DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss.SSS");
				LocalDateTime dateTime = LocalDateTime.from(f.parse(startDateArg));
				TimeDuration durationArg = TimeDuration.valueOf(commandLine.getOptionValue("duration").toUpperCase());
				Long thresholdArg = Long.valueOf(commandLine.getOptionValue("threshold"));
				String accessLog = commandLine.getOptionValue("accesslog");
				if(StringUtils.isBlank(accessLog)) {
					accessLog = "access.log";
				}
				System.out.println("Starting import of " + accessLog);
				logRecordCSVParser.importCSVFile(accessLog);
				System.out.println("Finsihed import of log files");
				List<String> results = logRecordCSVParser.findThreshold(dateTime, durationArg, thresholdArg);
				if(results == null) {
					throw new IllegalStateException("Results should not be null");
				}
				if(results.size() > 0) {
					System.out.println("The following IP addresses exceeded threshold:");
				}
				results.stream().forEach(result->System.out.println(result));
			} catch (ParseException e) {
				System.out.println(e.getMessage());
				formatter.printHelp("com.ef.Parser", options);
				System.exit(1);
			}

		}catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}
}
