-- Add a trigger to update the UpdatedAt column whenever a row in the Users table is updated
CREATE
OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.UpdatedAt
= CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$
language 'plpgsql';

CREATE
OR REPLACE  TRIGGER  update_users_updated_at BEFORE
UPDATE
    ON Users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE
OR REPLACE  TRIGGER update_groups_updated_at BEFORE
UPDATE
    ON Groups FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE
OR REPLACE  TRIGGER update_friendship_updated_at BEFORE
UPDATE
    ON Friendships FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
/