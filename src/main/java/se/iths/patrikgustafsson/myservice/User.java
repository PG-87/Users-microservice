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

    @Id @GeneratedValue long id;
    String name;
    String adress;
    String mail;
}
