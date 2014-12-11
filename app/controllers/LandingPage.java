package controllers;

import play.*;
import play.mvc.*;

public class LandingPage extends Controller {

    public static Result get() {
        return ok("Welcome to the OER World Map!");
    }

}
