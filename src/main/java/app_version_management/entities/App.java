package app_version_management.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class App {
    private String name;
    private String id;
}
