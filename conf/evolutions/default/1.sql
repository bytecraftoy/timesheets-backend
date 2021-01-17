# --- !Ups
CREATE TABLE app_user
(
	app_user_id uuid NOT NULL
		CONSTRAINT app_user_pk
			PRIMARY KEY,
	username text NOT NULL,
	first_name text NOT NULL,
	last_name text NOT NULL,
	email text DEFAULT NULL,
	phone_number text DEFAULT NULL ,
	salary numeric(20,2) DEFAULT NULL,
	is_manager boolean DEFAULT FALSE NOT NULL,
	timestamp_created timestamp NOT NULL,
	timestamp_edited timestamp NOT NULL
);




CREATE TABLE project (
                    project_id uuid NOT NULL,
                    name text NOT NULL,
                    description text DEFAULT NULL,
                    timestamp_created timestamp NOT NULL,
                    timestamp_edited timestamp NOT NULL,
                    billable boolean NOT NULL DEFAULT TRUE,
                    owned_by uuid NOT NULL,
                    created_by uuid NOT NULL,
                    last_edited_by uuid NOT NULL,
                    client_id uuid NOT NULL,
                    CONSTRAINT project_pk PRIMARY KEY (project_id)

);

CREATE TABLE project_app_user (
                               project_id uuid NOT NULL,
                               app_user_id uuid NOT NULL,
                               CONSTRAINT project_app_user_pk
                                PRIMARY KEY (project_id,
                                             app_user_id)
);

ALTER TABLE project_app_user
    ADD CONSTRAINT project_fk FOREIGN KEY (project_id)
    REFERENCES project (project_id)
    ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE project_app_user ADD CONSTRAINT app_user_fk FOREIGN KEY (app_user_id)
    REFERENCES app_user (app_user_id)
    ON DELETE CASCADE ON UPDATE CASCADE;


CREATE TABLE client (
                    client_id uuid NOT NULL,
                    name text,
                    email VARCHAR(256),
                    timestamp_created timestamp,
                    timestamp_edited timestamp,
                    CONSTRAINT client_pk PRIMARY KEY (client_id)
);

CREATE UNIQUE INDEX idx_client_email ON client(email);

ALTER TABLE project ADD CONSTRAINT client_fk FOREIGN KEY (client_id)
    REFERENCES client (client_id)
    ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE project ADD CONSTRAINT owned_by_reference FOREIGN KEY (owned_by)
    REFERENCES app_user (app_user_id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE project ADD CONSTRAINT created_by_reference FOREIGN KEY (created_by)
    REFERENCES app_user (app_user_id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE project ADD CONSTRAINT last_edited_by_reference FOREIGN KEY (last_edited_by)
    REFERENCES app_user (app_user_id)
    ON DELETE CASCADE ON UPDATE CASCADE;

# --- !Downs
ALTER TABLE project_app_user DROP CONSTRAINT IF EXISTS project_fk;
ALTER TABLE project_app_user DROP CONSTRAINT IF EXISTS user_fk;
DROP TABLE IF EXISTS project_app_user;
ALTER TABLE project DROP CONSTRAINT IF EXISTS client_fk;
ALTER TABLE project DROP CONSTRAINT IF EXISTS owned_by_reference;
ALTER TABLE project DROP CONSTRAINT IF EXISTS created_by_reference;
ALTER TABLE project DROP CONSTRAINT IF EXISTS last_edited_by_reference;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS app_user CASCADE;
DROP TABLE IF EXISTS client CASCADE;
