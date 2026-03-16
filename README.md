**Реализация практического задания по результатам пятого модуля в курсе Apache Kafka для разработки и архитектуры**
***

В файле `tls/gen-tls.sh` описан скрипт, создающий файлы сертификатов, truststore, keystore


Проект может быть запущен через docker compose. Сначала запускаем `zookeeper`, `kafka-0`, `kafka-1`, `kafka-2`:
```shell
docker compose up zookeeper kafka-0 kafka-1 kafka-2
```

Для создания топиков можно воспользоватся утилитой kafka-topics из контейнера kafka-0, указав файл конфигурации,
проброшенный внутрь контейнера, поскольку все listener настроены на SSL
```shell
docker exec kafka-0 kafka-topics --bootstrap-server kafka-0:9092 \
  --create --topic topic-1 --partitions 3 --replication-factor 3 \
  --command-config /etc/kafka/secrets/adminclient-configs.conf
```

Получаем вывод:
```shell
Created topic topic-1.
```

Повторяем для topic-2:
```shell
docker exec kafka-0 kafka-topics --bootstrap-server kafka-0:9092 \
  --create --topic topic-2 --partitions 3 --replication-factor 3 \
  --command-config /etc/kafka/secrets/adminclient-configs.conf
```

Вывод:
```shell
Created topic topic-2.
```

Настраиваем права доступа:
* Топик topic-1 должен быть доступен и для consumer и для producer
* Топик topic-2 должен быть доступен для отправки сообщений от producer но недоступен для consumer для чтения данных

В данном решении аутентификация производится с помощью клиентских сертификатов, где CN - это логин. Consumer и Producer
были взяты из решения к уроку 1. Это - java приложения, для которых созданы отдельные сертификаты
(app-consumer/app-producer). Для инстансов брокеров созданы отдельные сертификаты kafka-0, kafka-1, kafka-2 с
соответствующими CN, которые указаны как суперюзеры.

Разрешаем `app-producer` писать в `topic-1`:
```shell
docker exec kafka-0 kafka-acls --bootstrap-server kafka-0:9092 \
  --command-config /etc/kafka/secrets/adminclient-configs.conf \
  --add --allow-principal User:app-producer --operation Write --topic topic-1
```
Вывод
```shell
Adding ACLs for resource `ResourcePattern(resourceType=TOPIC, name=topic-1, patternType=LITERAL)`: 
        (principal=User:app-producer, host=*, operation=WRITE, permissionType=ALLOW) 

Current ACLs for resource `ResourcePattern(resourceType=TOPIC, name=topic-1, patternType=LITERAL)`: 
        (principal=User:app-producer, host=*, operation=WRITE, permissionType=ALLOW) 
```

Разрешаем `app-producer` писать в `topic-2`:
```shell
docker exec kafka-0 kafka-acls --bootstrap-server kafka-0:9092 \
  --command-config /etc/kafka/secrets/adminclient-configs.conf \
  --add --allow-principal User:app-producer --operation Write --topic topic-2
```
Вывод
```shell
Adding ACLs for resource `ResourcePattern(resourceType=TOPIC, name=topic-2, patternType=LITERAL)`: 
        (principal=User:app-producer, host=*, operation=WRITE, permissionType=ALLOW) 

Current ACLs for resource `ResourcePattern(resourceType=TOPIC, name=topic-2, patternType=LITERAL)`: 
        (principal=User:app-producer, host=*, operation=WRITE, permissionType=ALLOW) 
```

Разрешаем `app-consumer` читать из `topic-1`:

```shell
docker exec kafka-0 kafka-acls --bootstrap-server kafka-0:9092 \
  --command-config /etc/kafka/secrets/adminclient-configs.conf \
  --add --allow-principal User:app-consumer --operation Read --topic topic-1 --group group1
```
Вывод
```shell
Adding ACLs for resource `ResourcePattern(resourceType=TOPIC, name=topic-1, patternType=LITERAL)`: 
        (principal=User:app-consumer, host=*, operation=READ, permissionType=ALLOW) 

Current ACLs for resource `ResourcePattern(resourceType=TOPIC, name=topic-1, patternType=LITERAL)`: 
        (principal=User:app-producer, host=*, operation=WRITE, permissionType=ALLOW)
        (principal=User:app-consumer, host=*, operation=READ, permissionType=ALLOW) 
```

Даём доступ `app-consumer` к consumer группе `group1`

```shell
docker exec kafka-0 kafka-acls --bootstrap-server kafka-0:9092 \
  --command-config /etc/kafka/secrets/adminclient-configs.conf \
  --add --allow-principal User:app-consumer --operation Read --group group1
  
docker exec kafka-0 kafka-acls --bootstrap-server kafka-0:9092 \
  --command-config /etc/kafka/secrets/adminclient-configs.conf \
  --add --allow-principal User:app-consumer --operation Describe --group group1
```

Вывод:
```shell
Adding ACLs for resource `ResourcePattern(resourceType=GROUP, name=group1, patternType=LITERAL)`: 
        (principal=User:app-consumer, host=*, operation=READ, permissionType=ALLOW) 

Current ACLs for resource `ResourcePattern(resourceType=GROUP, name=group1, patternType=LITERAL)`: 
        (principal=User:app-consumer, host=*, operation=READ, permissionType=ALLOW) 
        
Adding ACLs for resource `ResourcePattern(resourceType=GROUP, name=group1, patternType=LITERAL)`: 
        (principal=User:app-consumer, host=*, operation=DESCRIBE, permissionType=ALLOW) 

Current ACLs for resource `ResourcePattern(resourceType=GROUP, name=group1, patternType=LITERAL)`: 
        (principal=User:app-consumer, host=*, operation=DESCRIBE, permissionType=ALLOW)
        (principal=User:app-consumer, host=*, operation=READ, permissionType=ALLOW) 
```


Запускаем `app-producer-1` и `app-producer-2` которые настроены чтобы писать сообщения в топики `topic-1` и `topic-2`
соответственно:
```shell
docker compose up app-producer-1 app-producer-2
```

Видим вывод:
```shell
app-producer-2  | Пишем в kafka-0:9092 -> topic-2
app-producer-1  | Пишем в kafka-0:9092 -> topic-1
app-producer-1  | SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
app-producer-1  | SLF4J: Defaulting to no-operation (NOP) logger implementation
app-producer-1  | SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
app-producer-2  | SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
app-producer-2  | SLF4J: Defaulting to no-operation (NOP) logger implementation
app-producer-2  | SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
app-producer-1  | Отправлено сообщение: SimpleMessage{text='Hello world 1'}
app-producer-2  | Отправлено сообщение: SimpleMessage{text='Hello world 1'}
app-producer-2  | Отправлено сообщение: SimpleMessage{text='Hello world 2'}
app-producer-1  | Отправлено сообщение: SimpleMessage{text='Hello world 2'}
app-producer-1  | Отправлено сообщение: SimpleMessage{text='Hello world 3'}
app-producer-2  | Отправлено сообщение: SimpleMessage{text='Hello world 3'}
app-producer-1  | Отправлено сообщение: SimpleMessage{text='Hello world 4'}
```

Пробуем запустить `app-consumer`, который будет читать `topic-1`:
```shell
docker compose up app-consumer-1
```