package aws.sqs.example;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@SpringBootApplication
public class ExampleApplication {
	private final ObjectMapper objectMapper;

	public ExampleApplication(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

	@Bean
	public Consumer<SQSEvent> myFunc() {
		return this::handleEvent;
	}

	private void handleEvent(SQSEvent event) {
		var messages = event.getRecords();
		var bodies = messages.stream().map(SQSEvent.SQSMessage::getBody);
		bodies.map(this::getMyDtoFromMessageBody).forEach(System.out::println);
	}

	private myDto getMyDtoFromMessageBody(String v) {
		try {
			return objectMapper.readValue(v, myDto.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private record myDto(Long id, String name) {}
}
