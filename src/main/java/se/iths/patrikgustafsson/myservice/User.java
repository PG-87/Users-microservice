package se.iths.patrikgustafsson.myservice;

import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id @GeneratedValue int id;
    String username;
    String realName;
    String mail;
    float income;
    boolean inRelationship;
}
