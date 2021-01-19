# --- !Ups

CREATE TABLE timeinput
(
    timeinput_id UUID NOT NULL,
    app_user_id UUID NOT NULL,
    project_id UUID NOT NULL,
    input_date DATE NOT NULL,
    minutes INTEGER DEFAULT NULL,
    description VARCHAR(100) DEFAULT NULL,
    timestamp_created TIMESTAMP NOT NULL,
    timestamp_edited TIMESTAMP NOT NULL,
    CONSTRAINT timeinput_pk PRIMARY KEY (timeinput_id),
    CONSTRAINT timeinput_app_user_fk FOREIGN KEY (app_user_id) REFERENCES app_user(app_user_id),
    CONSTRAINT timeinput_project_fk FOREIGN KEY (project_id) REFERENCES project(project_id)
);

CREATE UNIQUE INDEX idx_timeinput_unique ON timeinput(app_user_id, project_id, input_date);


# --- !Downs

DROP INDEX idx_timeinput_unique;
DROP TABLE timeinput;
