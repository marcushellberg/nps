-- Indexes for common lookups
CREATE INDEX idx_target_email_address ON target_emails(email_address);
CREATE INDEX idx_survey_link_token ON survey_links(token);
CREATE INDEX idx_survey_link_email ON survey_links(email_address);

-- Index for faster NPS score calculations
CREATE INDEX idx_responses_score ON responses(score);

-- Index for faster survey response counting
CREATE INDEX idx_survey_links_survey_id ON survey_links(survey_id);

-- Index for finding unsent surveys
CREATE INDEX idx_surveys_send_date ON surveys(send_date) WHERE send_date IS NULL;