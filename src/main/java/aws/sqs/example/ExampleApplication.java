package aws.sqs.example;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@SpringBootApplication
public class ExampleApplication {

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
		bodies.forEach(System.out::println);
//		throw new RuntimeException("ERROR"); - if exception is thrown, message would be added to dlq
	}
}
