package org.learn.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + "]";
    }
}