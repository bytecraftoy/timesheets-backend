# --- !Ups
INSERT INTO app_user (first_name,
                      last_name,
                      is_manager,
                      timestamp_created,
                      timestamp_edited)
VALUES ('Eka', 'E_sukunimi', TRUE, '2004-10-19 10:23:54', '2004-10-19 10:23:54'),
       ('Toka', 'T_sukunimi', TRUE, '2004-10-20 11:30:45', '2012-11-19 16:27:01');

INSERT INTO client (name,
                    email,
                    timestamp_created,
                    timestamp_edited)
VALUES ('Esimerkkiasiakas',
        'asiakas@yritys.fi',
        '2004-10-20 11:30:45',
        '2012-11-19 16:27:01');

INSERT INTO project (name,
                     description,
                     timestamp_created,
                     timestamp_edited,
                     billable,
                     owned_by,
                     created_by,
                     last_edited_by,
                     client_id)
VALUES ('Testi_projekti',
        'Testi-projektin kuvaus',
        '2004-10-19 10:23:54',
        '2004-10-19 10:23:54',
        TRUE,
        1,
        2,
        2,
        1);

# --- !Downs

DELETE FROM project WHERE name='Testi_projekti';
DELETE FROM client WHERE name='Esimerkkiasiakas';
DELETE FROM app_user WHERE first_name='Toka';
DELETE FROM app_user WHERE first_name='Eka';
