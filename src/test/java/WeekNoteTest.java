import api.WeekNotesApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static api.AuthApi.login;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static testdata.Accounts.*;

public class WeekNoteTest {

    @Test
    void canCreateWeekNoteAndReadItAsOwner() {
        String date = "2026-09-14";
        String note = "this is a note generated from test";
        WeekNotesApi owner = new WeekNotesApi(login(OWNER_USERNAME, OWNER_PASSWORD));

        owner.updateWeekNotes(date, note);
        owner.getWeekNotes(date)
                .statusCode(200)
                .body("text", equalTo(note));
    }

    @Test
    void weekNoteStartMustBeAMonday() {
        String date = "2026-09-16";
        String note = "negative test " + UUID.randomUUID();
        WeekNotesApi owner = new WeekNotesApi(login(OWNER_USERNAME, OWNER_PASSWORD));

        owner.updateWeekNotes(date, note).statusCode(400);
        assertNotEquals(note, owner.getWeekNotes(date).extract().body().path("text"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2026-13-01",
            "2026-01-32",
            "2026-02-30",
            "2026-00-01",
            "2026-01-00"
    })
    void weekStartMustBeAValidDate(String invalidDate) {
        String note = "negative test " + UUID.randomUUID();
        WeekNotesApi owner = new WeekNotesApi(login(OWNER_USERNAME, OWNER_PASSWORD));

        owner.updateWeekNotes(invalidDate, note).statusCode(400);
        assertNotEquals(note, owner.getWeekNotes(invalidDate).extract().body().path("text"));
    }

    @Test
    void everyOwnerCanSeeAndUpdateOnlyTheirWeekNotes() {
        String ownerNote = "owner " + UUID.randomUUID();
        String nonOwnerNote = "non owner " + UUID.randomUUID();
        String date = "2026-10-05";

        WeekNotesApi owner = new WeekNotesApi(login(OWNER_USERNAME, OWNER_PASSWORD));
        WeekNotesApi nonOwner = new WeekNotesApi(login(OTHER_USERNAME, OTHER_PASSWORD));

        owner.updateWeekNotes(date, ownerNote).statusCode(200);
        nonOwner.updateWeekNotes(date, nonOwnerNote).statusCode(200);

        assertEquals(ownerNote, owner.getWeekNotes(date).extract().body().path("text"));
        assertEquals(nonOwnerNote, nonOwner.getWeekNotes(date).extract().body().path("text"));
    }

}
