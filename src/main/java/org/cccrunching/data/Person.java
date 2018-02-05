package org.cccrunching.data;

import java.util.Objects;

public class Person {

    private final String name;
    private final String title;

    public Person(String name){
        this(name,null);
    }

    public Person(String name, String title){
        this.name   = name;
        this.title  = title;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name) &&
                Objects.equals(title, person.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, title);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
