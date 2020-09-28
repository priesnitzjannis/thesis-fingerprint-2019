package de.dali.thesisfingerprint2019.logging.SQLite.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "person")
public class Person {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long personID;

    @NonNull
    private String name;

    @NonNull
    private String gender;

    @NonNull
    private short age;

    // [Fitzpatrick scale](https://en.wikipedia.org/wiki/Fitzpatrick_scale)
    @NonNull
    private short skinColor;


    public long getPersonID() {
        return personID;
    }

    public void setPersonID(long personID) {
        this.personID = personID;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getGender() {
        return gender;
    }

    public void setGender(@NonNull String gender) {
        this.gender = gender;
    }

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }

    public short getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(short skinColor) {
        this.skinColor = skinColor;
    }

    public Person(@NonNull String name, @NonNull String gender, short age, short skinColor) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.skinColor = skinColor;
    }
}
