package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.examples.Utils;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import helpers.JSONForm;
import io.michaelallen.mustache.MustacheFactory;
import io.michaelallen.mustache.api.Mustache;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.validator.routines.EmailValidator;

import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Result;

import helpers.Countries;

public class UserIndex extends OERWorldMap {

  public static Result get() throws IOException {
    Map<String, Object> data = new HashMap<>();
    data.put("countries", Countries.list(currentLocale));
    return ok(render("Registration", data, "UserIndex/index.mustache"));
  }

  public static Result post() throws IOException, ProcessingException {

    Map<String, Object> data = new HashMap<>();

    JsonNode personInstance = JSONForm.parseFormData(request().body().asFormUrlEncoded());

    Resource person = Resource.fromJson(personInstance);
    ProcessingReport report = person.validate();

    if (!report.isSuccess()) {
      System.out.println(report);
      data.put("errors", JSONForm.generateErrorReport(report));
    }

    data.put("person", person);

    return ok(render("Registration", data, "UserIndex/index.mustache"));
    //return ok(report.toString());

  }

  public static Result confirm(String id) throws IOException {

    Resource user;
    Map<String,Object> data = new HashMap<>();

    try {
      user = mUnconfirmedUserRepository.deleteResource(id);
    } catch (IOException e) {
      e.printStackTrace();
      data.put("status", "warning");
      data.put("message", "Error confirming email address");
      return ok(render("Registration", data, "feedback.mustache"));
    }

    resourceRepository.addResource(user);
    data.put("status", "success");
    data.put("message", "Thank you for your interest in the OER World Map. Your email address <em>"
            + user.get("email") + "</em> has been confirmed."
    );
    return ok(render("Registration", data, "feedback.mustache"));

  }

}
