# --- !Ups
INSERT INTO app_user (app_user_id,
                      username,
                      first_name,
                      last_name,
                      is_manager,
                      timestamp_created,
                      timestamp_edited)
VALUES ('9fa407f4-7375-446b-92c6-c578839b7780',
        'ekakäyttäjä',
        'Eka',
        'E_sukunimi',
        TRUE,
        '2004-10-19 10:23:54',
        '2004-10-19 10:23:54'),
       ('06be4b85-8f65-4f65-8965-faba1216f199',
        'tokakäyttäjä',
        'Toka',
        'T_sukunimi',
        TRUE,
        '2004-10-20 11:30:45',
        '2012-11-19 16:27:01');

INSERT INTO client (client_id,
                    name,
                    email,
                    timestamp_created,
                    timestamp_edited)
VALUES ('1bb44a7e-cd7c-447d-a9e9-26495b52fa88',
        'Esimerkkiasiakas',
        'asiakas@yritys.fi',
        '2004-10-20 11:30:45',
        '2012-11-19 16:27:01');

INSERT INTO project (project_id,
                     name,
                     description,
                     timestamp_created,
                     timestamp_edited,
                     billable,
                     owned_by,
                     created_by,
                     last_edited_by,
                     client_id)
VALUES ('a3eb6db5-5212-46d0-bd08-8e852a45e0d3',
        'Testi_projekti',
        'Testi-projektin kuvaus',
        '2004-10-19 10:23:54',
        '2004-10-19 10:23:54',
        TRUE,
        '9fa407f4-7375-446b-92c6-c578839b7780',
        '06be4b85-8f65-4f65-8965-faba1216f199',
        '06be4b85-8f65-4f65-8965-faba1216f199',
        '1bb44a7e-cd7c-447d-a9e9-26495b52fa88');

# --- !Downs

DELETE FROM project WHERE name='Testi_projekti';
DELETE FROM client WHERE name='Esimerkkiasiakas';
DELETE FROM app_user WHERE first_name='Toka';
DELETE FROM app_user WHERE first_name='Eka';
