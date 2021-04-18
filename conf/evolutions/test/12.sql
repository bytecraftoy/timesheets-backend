# --- !Ups

ALTER TABLE project ADD hourly_cost numeric(27,9) DEFAULT 0;
ALTER TABLE project ADD currency text DEFAULT 'EUR';

UPDATE project SET hourly_cost = 1.009, currency = 'EUR' WHERE project_id = '40726707-57d1-47e0-a82b-6d85320b160b';
UPDATE project SET hourly_cost = 24.99, currency = 'EUR' WHERE project_id = 'd7e738a2-60cf-4336-b7d9-5216ef960e3a';
UPDATE project SET hourly_cost = 0.0000339, currency = 'EUR' WHERE project_id = 'a3eb6db5-5212-46d0-bd08-8e852a45e0d3';
UPDATE project SET hourly_cost = 1000.000001, currency = 'EUR' WHERE project_id = 'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9';
UPDATE project SET hourly_cost = 20.99, currency = 'EUR' WHERE project_id = 'd9be4f9a-029c-43ed-9a4e-d9379fa86a00';

# --- !Downs

UPDATE project SET hourly_cost = NULL;
UPDATE project SET currency = NULL;
ALTER TABLE project DROP COLUMN IF EXISTS currency;
ALTER TABLE project DROP COLUMN IF EXISTS hourly_cost;
