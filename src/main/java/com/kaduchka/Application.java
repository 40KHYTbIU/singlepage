package com.kaduchka;

import com.kaduchka.common.Direction;
import com.kaduchka.common.DirectionPropertyEditor;
import com.kaduchka.common.Fields;
import com.kaduchka.common.FieldsPropertyEditor;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {

	@Bean
	public CustomEditorConfigurer customEditorConfigurer(){
		CustomEditorConfigurer configurer = new CustomEditorConfigurer();
		Map<Class<?>, Class<? extends PropertyEditor>>  propertyEditorMap = new HashMap<>();

		propertyEditorMap.put(Direction.class, DirectionPropertyEditor.class);
		propertyEditorMap.put(Fields.class, FieldsPropertyEditor.class);

		configurer.setCustomEditors(propertyEditorMap);
		return configurer;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
