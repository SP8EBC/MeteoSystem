package cc.pogoda.mobile.pogodacc.web.deserializer;

import android.content.Intent;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Calendar;

import cc.pogoda.mobile.pogodacc.type.CustomLocalDateTime;

public class CustomLocalDateTimeDeserializer implements JsonDeserializer <CustomLocalDateTime> {
    @Override
    public CustomLocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        CustomLocalDateTime out = new CustomLocalDateTime();

        out.calendar = Calendar.getInstance();

        JsonObject object = json.getAsJsonObject();

        Integer year_int =  object.getAsJsonPrimitive("year").getAsInt();
        Integer month_int = object.getAsJsonPrimitive("monthValue").getAsInt();
        Integer day_int = object.getAsJsonPrimitive("dayOfMonth").getAsInt();
        Integer hour_int = object.getAsJsonPrimitive("hour").getAsInt();
        Integer minute_int = object.getAsJsonPrimitive("minute").getAsInt();
        Integer second_int = object.getAsJsonPrimitive("second").getAsInt();

        out.calendar.set(year_int, month_int, day_int, hour_int, minute_int, second_int);
        out.origin = object;

        return out;
    }
}
