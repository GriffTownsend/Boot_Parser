package com.ef.Parser.components;

import com.ef.Parser.dao.LogRecordRepository;
import com.ef.Parser.model.LogRecord;
import com.ef.Parser.model.TimeDuration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class LogRecordCSVParser {

	private static final String[] DATE_PATTERNS={DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern(), "yyyy-MM-dd'T'HH:mm:ss'Z'", DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(), DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern(), "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"};

	@Resource
	private LogRecordRepository recordRepository;

	public void importCSVFile(String pathName) {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(pathName));
			 CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
			 	.withHeader("date", "ipAddress", "request", "status","userAgent").withSkipHeaderRecord(false).withTrim().withDelimiter('|')
			 ))
		{
			Iterator<CSVRecord> records = parser.iterator();
			final List<LogRecord> logRecords = new ArrayList<>();
			records.forEachRemaining(record -> {
				LocalDateTime date = parseDateTime(record.get("date"));
				String ipAddress = record.get("ipAddress");
				String request = record.get("request");
				Integer status = Integer.valueOf(record.get("status"));
				String userAgent = record.get("userAgent");

				LogRecord logRecord = new LogRecord(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()), ipAddress, request, status, userAgent);
				logRecords.add(logRecord);
				if(logRecords.size()==200) {
					recordRepository.saveAll(logRecords);
					logRecords.clear();
				}
			});
			if(logRecords.size() > 0) {
				recordRepository.saveAll(logRecords);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> findThresholdByDuration(String startDate, TimeDuration duration, Long threshold) {
		List<String> results = new ArrayList<>();
		if(org.apache.commons.lang3.StringUtils.isBlank(startDate)) {
			throw new IllegalArgumentException("Start Date not provided");
		}
		Assert.notNull(duration, "Duration cannot be null");
		Assert.notNull(threshold, "Threshold cannot be null");
		LocalDateTime start = parseDateTime(startDate);
		LocalDateTime end = (TimeDuration.DAILY.equals(duration))? start.plusDays(1) : start.plusHours(1);
		return recordRepository.findThresholdByDuration(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()),
				Date.from(end.atZone(ZoneId.systemDefault()).toInstant()), threshold);
	}

	public LocalDateTime parseDateTime(String date) {
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.from(f.parse(date));
		return dateTime;
	}

	public LogRecordRepository getRepository() {
		return this.recordRepository;
	}
}
