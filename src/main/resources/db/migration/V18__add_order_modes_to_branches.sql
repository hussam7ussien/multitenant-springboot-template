-- Add order mode flags to branches table
ALTER TABLE branches ADD COLUMN eat_in_enabled BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE branches ADD COLUMN delivery_enabled BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE branches ADD COLUMN takeaway_enabled BOOLEAN NOT NULL DEFAULT true;
