CREATE TABLE target_lists
(
    id      BIGSERIAL PRIMARY KEY,
    version BIGINT       NOT NULL,
    name    VARCHAR(255) NOT NULL
);

CREATE TABLE target_emails
(
    id             BIGSERIAL PRIMARY KEY,
    version        BIGINT       NOT NULL,
    email_address  VARCHAR(255) NOT NULL,
    target_list_id BIGINT       NOT NULL,
    CONSTRAINT fk_target_list
        FOREIGN KEY (target_list_id)
            REFERENCES target_lists (id)
);

CREATE TABLE surveys
(
    id             BIGSERIAL PRIMARY KEY,
    version        BIGINT       NOT NULL,
    name           VARCHAR(255) NOT NULL,
    question       TEXT         NOT NULL,
    email_subject  VARCHAR(255),
    email_body     TEXT,
    send_date      TIMESTAMP,
    total_sent     INTEGER,
    target_list_id BIGINT,
    CONSTRAINT fk_survey_target_list
        FOREIGN KEY (target_list_id)
            REFERENCES target_lists (id)
);

CREATE TABLE survey_links
(
    id            BIGSERIAL PRIMARY KEY,
    version       BIGINT       NOT NULL,
    token         VARCHAR(255) NOT NULL UNIQUE,
    email_address VARCHAR(255) NOT NULL,
    created_date  TIMESTAMP    NOT NULL,
    survey_id     BIGINT       NOT NULL,
    CONSTRAINT fk_survey
        FOREIGN KEY (survey_id)
            REFERENCES surveys (id)
);

CREATE TABLE responses
(
    id              BIGSERIAL PRIMARY KEY,
    version         BIGINT    NOT NULL,
    score           INTEGER   NOT NULL,
    submission_date TIMESTAMP NOT NULL,
    survey_link_id  BIGINT    NOT NULL UNIQUE,
    CONSTRAINT fk_survey_link
        FOREIGN KEY (survey_link_id)
            REFERENCES survey_links (id),
    CONSTRAINT valid_score
        CHECK (score >= 0 AND score <= 10)
);