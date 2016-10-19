[travis]:https://travis-ci.org/Ensemplix/Monsoon
[codecov]:https://codecov.io/github/Ensemplix/Monsoon?branch=v2.0

[travis img]:https://travis-ci.org/Ensemplix/Monsoon.svg?branch=v2.0
[codecov img]:https://codecov.io/github/Ensemplix/Monsoon/coverage.svg?branch=v2.0

Monsoon [![][travis img]][travis] [![][codecov img]][codecov]
============

Мы, как никто другие, знаем все сложности при создании новых команд. В нашем проекте используется дюжина различных команд.
Отличие этой библиотеки от любых других заключаются в генерации объектов на лету, без необходимости парсить строку аргументов
от пользователя. Нужно лишь один раз написать парсер и библиотека будет сама преобразовывать аргументы в объекты.

## Подключение

Для подключения в своем проекте необходимо использовать __Maven__ или __Gradle__, а также использовать __Java 8__.

### Maven
```xml
<repositories>
    <repository>
        <id>Ensemplix</id>
        <url>http://maven.ensemplix.ru/artifactory/Ensemplix</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ru.ensemplix.command</groupId>
        <artifactId>Monsoon</artifactId>
        <version>1.2</version>
    </dependency>
</dependencies>
```

### Gradle
```gradle
repositories {
    maven {
        url 'http://maven.ensemplix.ru/artifactory/Ensemplix/'
    }
}

dependencies {
   compile 'ru.ensemplix.command:Monsoon:1.2'
}
```
## Использование
Дальше приведены примеры для работы с библиотекой.
### Создание команды
Количество имен команды неограниченно, но они не должны повторяться. Если команда не будет найдена, то будет выброшено исключение ```CommandNotFoundException```.
Количество подкоманд в объекте неограниченно.
```java
// Для работы с командами нужно унаследоваться от интерфейса CommandSender.
public class FooSender implements CommandSender {
    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }
}
```
```java
// Вот так выглядит создание команды.
public class FooCommand {
    @Command
    public void hello(FooSender sender, String name) {
        sender.sendMessage("Hello " + name);
    }
}
```
```java
// Создаем обработчик команд.
CommandDispatcher dispatcher = new CommandDispatcher();
// Регистрируем команду.
dispatcher.register(new FooCommand(), "test");
// Вызываем команду.
dispatcher.call(new FooSender(), "/test hello koala")
```
```
Hello Koala
```
### Парсер объектов
Библиотека может конвертировать не только простые типы в объекты, но для этого нужно написать парсер.
```java
public class FooRegion {
    private final String name;

    public FooRegion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```
```java
public class FooRegionParser implements TypeParser<FooRegion> {
    @Override
    public FooRegion parse(String value) {
        return new FooRegion(value);
    }
}
```
```java
public class FooRegionCommand {
   @Command
   public void create(FooSender sender, FooRegion region) {
       sender.sendMessage("You created region " + region.getName());
   }
}
```
```java
// Регистрируем команду.
dispatcher.register(new FooRegionCommand(), "region");
// Регистрируем парсер.
dispatcher.bind(new FooRegionParser());
// Вызываем команду.
dispatcher.call(new FooSender(), "/region create moon");
```
```
You created region moon
```
### Перечисление
Перечисление множества происходит с помощью коллекции. Поддерживаются любые типы объектов.
```java
public class FooCollection {
    @Command
    public void test(FooSender sender, Collection<String> players) {
        for(String player : players) {
            System.out.println(player);
        }
    }
}
```
```java
// Регистрируем команду.
dispatcher.register(new FooCollection(), "collection");
// Вызываем команду.
dispatcher.call(new FooSender(), "/collection test Ensirius Invincible Koala Elon");
```
```
Ensirius
Invincible
Koala
Elon
```
### Ограничение доступа
По умолчанию команда доступна всем пользователям. Для включения проверки прав необходимо в аннотации ```@Command``` выставить ```permission = true```. При отсутствии прав на команду будет выброшено исключение ```CommandAccessException```.
```java
public class PrivateCommand {
    @Command(permission = true)
    public void test(PrivateSender sender) {
        sender.sendMessage("You are koala!");
    }
}
```
Определение логики проверки прав происходит в ```CommandSender```, в методе ```canUseCommand```.
```java
public class PrivateSender implements CommandSender {
    private String player;

    public PrivateSender(String player) {
        this.player = player;
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Ovrride
    public boolean canUseCommand(String command, String action) {
        if(player.equalsIgnoreCase("Koala")) {
            return true;
        }

        return false;
    }
}
```
```java
// Регистрируем команду.
dispatcher.register(new PrivateCommand(), "private");
// Вызываем команду.
dispatcher.call(new PrivateSender("koala"), "/private test koala");
```
```
You are Koala!
```
### Автодополнение
Автодополнение показывает возможные варианты дополнения команды.

```java
public class FooActionsCommand {
    @Command
    public void list(CommandSender sender) {

    }

    @Command
    public void view(CommandSender sender) {

    }

    @Command
    public void add(CommandSender sender) {

    }

    @Command
    public void addMember(CommandSender sender) {

    }
}
```
```java
// Регистрируем команду.
dispatcher.register(new FooActionsCommand(), "actions");
// Вызываем автодополнение.
Collection<String> options = dispatcher.complete(new FooSender(), "/actions ad");
// Отображаем варианты.
System.out.println(options);
```
```
[add, addMember]
```
Для автодополнения объектов нужно написать парсер.
```java
public class FooRegionCompleter implements CommandCompleter {
    private static final ImmutableSet<String> regions = ImmutableSet.of("home", "spawn", "spawn123", "spb");

    @Override
    public Collection<String> complete(CommandContext context, String arg) {
        return regions.stream().filter(name -> name.startsWith(arg)).collect(Collectors.toList());
    }
}
```
```java
// Регистрируем команду.
dispatcher.register(new FooRegionCommand(), "region");
// Регистрируем парсер.
dispatcher.bind(new FooRegionCompleter());
// Вызываем автодополнение.
Collection<String> options = dispatcher.complete(new FooSender(), "/region create");
// Отображаем варианты.
System.out.println(options);
```
```
[home, spawn, spawn123, spb]
```
