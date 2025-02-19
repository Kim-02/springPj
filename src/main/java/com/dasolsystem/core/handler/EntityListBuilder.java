package com.dasolsystem.core.handler;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityListBuilder {
    private String id;
    private String name;
    private String result;

    public List<String> listBuild() {
        ArrayList<String> list = new ArrayList<>();
        list.add(id);
        list.add(name);
        list.add(result);
        return list;
    }
}
