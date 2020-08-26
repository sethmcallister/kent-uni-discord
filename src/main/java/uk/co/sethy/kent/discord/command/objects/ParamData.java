package uk.co.sethy.kent.discord.command.objects;

import lombok.Getter;
import uk.co.sethy.kent.discord.command.annotations.Param;

@Getter
public class ParamData {
    private String name;
    private boolean wildcard;
    private String defaultValue;
    private Class<?> parameterClass;

    public ParamData(Class<?> parameterClass, Param param) {
        this.name = param.name();
        this.wildcard = param.wildcard();
        this.defaultValue = param.defaultValue();
        this.parameterClass = parameterClass;
    }
}
