package io.github.pierre_ernst.fit.format;

import java.util.List;

import com.garmin.fit.Field;
import com.garmin.fit.Mesg;

import io.github.pierre_ernst.fit.model.FitData;
import io.github.pierre_ernst.fit.model.FitField;

public class Json {

	public static String formatFieldName(String s) {
		StringBuffer sb = new StringBuffer();
		String[] words = s.split("_");
		for (int i = 0; i < words.length; i++) {
			if (i == 0) {
				sb.append(words[i].toLowerCase());
			} else {
				sb.append(Character.toUpperCase(words[i].charAt(0)));
				sb.append(words[i].substring(1).toLowerCase());
			}
		}
		return sb.toString();
	}

	public static String format(FitData data) {
		StringBuffer sb = new StringBuffer();
		sb.append("{\n");
		String[] sections = data.keySet().toArray(new String[0]);
		for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
			if (sectionIndex > 0) {
				sb.append(",\n");
			}

			sb.append('"').append(formatFieldName(sections[sectionIndex])).append('"').append(":\n");

			List<Mesg> msgs = data.get(sections[sectionIndex]);
			sb.append("[\n");
			for (int msgIndex = 0; msgIndex < msgs.size(); msgIndex++) {
				if (msgIndex > 0) {
					sb.append(',');
				}
				sb.append("{\n");
				Mesg msg = msgs.get(msgIndex);
				boolean firstField = true;
				for (Field f : msg.getFields()) {
					if (!firstField) {
						sb.append(",\n");
					}
					firstField = false;
					String fieldName = formatFieldName(f.getName());
					if ("unknown".equals(fieldName)) {
						fieldName = "field-" + f.getNum();
					}
					sb.append('"').append(fieldName).append('"');
					sb.append(":");
					Object fieldValue = FitField.getValue(f);
					if (!(fieldValue instanceof Number)) {
						sb.append('"');
					}
					sb.append(fieldValue);
					if (!(fieldValue instanceof Number)) {
						sb.append('"');
					}
				}
				sb.append("\n}");

			}
			sb.append("\n]");

		}
		sb.append("\n}");
		return sb.toString();
	}
}
