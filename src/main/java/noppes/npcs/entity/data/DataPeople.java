package noppes.npcs.entity.data;

import java.util.Random;

public class DataPeople {
    private final static Random r = new Random();

    private static final Person[] people = new Person[] {
            new Person("Noppes", "Creator", "customnpcs:textures/entity/importantpeople/noppes.png"),
            new Person("Dati", "Patreon", "customnpcs:textures/entity/importantpeople/dati.png"),
            new Person("Vin0m", "Patreon", ""),
            new Person("Birb", "Patreon", ""),
            new Person("Flashback", "Patreon", ""),
            new Person("Ronan", "Patreon", ""),
            new Person("Shivaxi ", "Patreon", ""),
            new Person("GreatOrator", "Patreon", ""),
            new Person("Aphmau", "Patreon", ""),
            new Person("Kithoras", "Patreon", ""),
            new Person("Daniel N", "Patreon", ""),
            new Person("G1RCraft", "Patreon", ""),
            new Person("Joanie H", "Patreon", ""),
            new Person("Jaffra", "Patreon", ""),
            new Person("Orphie", "Patreon", ""),
            new Person("PPap", "Patreon", ""),
            new Person("RED9936", "Patreon", ""),
            new Person("NekoTune", "Patreon", ""),
            new Person("JusCallMeNico", "Patreon", "")
    };

    public static Person get(){
        return people[r.nextInt(people.length)];
    }

    static class Person{
        public final String name, title, skin;
        public Person(String name, String title, String skin){
            this.name = name;
            this.title = title;
            this.skin = skin;
        }
    }
}
