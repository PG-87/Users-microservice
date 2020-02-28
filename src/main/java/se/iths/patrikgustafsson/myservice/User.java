package se.iths.patrikgustafsson.myservice;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id @GeneratedValue Long id;
    String userName;
    String realName;
    String city;
    float income;
    boolean inRelationship;

    public User(Long id, String userName, String realName, String city, float income, boolean inRelationship) {
        this.id = id;
        this.userName = userName;
        this.realName = realName;
        this.city = city;
        this.income = income;
        this.inRelationship = inRelationship;
    }
}
