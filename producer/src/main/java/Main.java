import java.util.Random;

public class Main {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            throw new IllegalStateException("Необходимо передать аргументы: адрес брокера, топик");
        }

        String serverAddress = args[0];
        String topicName = args[1];

        System.out.println("Пишем в " + serverAddress + " -> " + topicName);

        try (var producer = new IntSequenceMessageProducer(serverAddress, topicName)) {
            while (true) {
                producer.emitNextMessage();
                Thread.sleep(RANDOM.nextInt(500, 5000));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
