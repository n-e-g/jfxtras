/**
 * Copyright (c) 2011-2020, JFXtras
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *    Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *    Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jfxtras.scene.control.agenda.icalendar.trial;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.icalendar.trial.controller.CalendarController;

/**
 * Demo of ICalendarAgenda with multiple events added and other controls to change displayed dates
 * 
 * @author David Bal
 *
 */
public class ICalendarAgendaAdvancedTrial extends Application
{
    private static LocalDate firstDayOfWeekLocalDate = getFirstDayOfWeekLocalDate();
    private static LocalDate getFirstDayOfWeekLocalDate()
    { // copied from AgendaWeekSkin
        Locale.setDefault(Locale.US);
        Locale myLocale = Locale.getDefault();
        WeekFields lWeekFields = WeekFields.of(myLocale);
        int lFirstDayOfWeek = lWeekFields.getFirstDayOfWeek().getValue();
        LocalDate lDisplayedDateTime = LocalDate.now();
        int lCurrentDayOfWeek = lDisplayedDateTime.getDayOfWeek().getValue();

        if (lFirstDayOfWeek <= lCurrentDayOfWeek) {
            lDisplayedDateTime = lDisplayedDateTime.plusDays(-lCurrentDayOfWeek + lFirstDayOfWeek);
        }
        else {
            lDisplayedDateTime = lDisplayedDateTime.plusDays(-lCurrentDayOfWeek - (7-lFirstDayOfWeek));
        }
        return lDisplayedDateTime;
    }
    
    public static void main(String[] args) {
        launch(args);       
    }

	@Override
	public void start(Stage primaryStage) throws IOException, TransformerException, ParserConfigurationException, SAXException
	{
        // ROOT PANE
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(ICalendarAgendaAdvancedTrial.class.getResource("view/Calendar.fxml"));
        BorderPane root = mainLoader.load();
        CalendarController controller = mainLoader.getController();
        Scene scene = new Scene(root, 1366, 768);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ICalendar Agenda Demo");
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.out.println(controller.vCalendar.toString())); // prints resulting VCALENDAR on close

        controller.setupData(firstDayOfWeekLocalDate, firstDayOfWeekLocalDate.plusDays(7));

    }
}
