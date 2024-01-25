-- Create Users Table
CREATE TABLE IF NOT EXISTS Users
(
    UserID       SERIAL PRIMARY KEY,
    Username     VARCHAR(255) UNIQUE NOT NULL,
    Email        VARCHAR(255) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255)        NOT NULL,
    FullName     VARCHAR(255)        NOT NULL,
    Gender       VARCHAR(10),
    DateOfBirth  DATE,
    CreatedAt    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- Create Friendships Table
CREATE TABLE IF NOT EXISTS Friendships
(
    FriendshipID SERIAL PRIMARY KEY,
    UserID1      INT                                                       NOT NULL,
    UserID2      INT                                                       NOT NULL,
    Status       TEXT CHECK (Status IN ('pending', 'accepted', 'blocked')) NOT NULL,
    CreatedAt    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user1 FOREIGN KEY (UserID1) REFERENCES Users (UserID) ON DELETE CASCADE,
    CONSTRAINT fk_user2 FOREIGN KEY (UserID2) REFERENCES Users (UserID) ON DELETE CASCADE,
    CHECK (UserID1 < UserID2),
    UNIQUE (UserID1, UserID2)
);

-- Create Groups Table
CREATE TABLE IF NOT EXISTS Groups
(
    GroupID         SERIAL PRIMARY KEY,
    GroupName       VARCHAR(255) NOT NULL,
    CreatedByUserID INT          NOT NULL,
    CreatedAt       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_group_creator FOREIGN KEY (CreatedByUserID) REFERENCES Users (UserID) ON DELETE CASCADE
);

-- Create GroupMembers Table
CREATE TABLE IF NOT EXISTS GroupMembers
(
    GroupMemberID SERIAL PRIMARY KEY,
    GroupID       INT                                      NOT NULL,
    UserID        INT                                      NOT NULL,
    JoinedAt      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    Role          TEXT CHECK (Role IN ('member', 'admin')) NOT NULL,
    CONSTRAINT fk_group FOREIGN KEY (GroupID) REFERENCES Groups (GroupID) ON DELETE CASCADE,
    CONSTRAINT fk_group_user FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE,
    UNIQUE (GroupID, UserID)
);

-- Create UserMessages Table
CREATE TABLE IF NOT EXISTS UserMessages
(
    MessageID       SERIAL PRIMARY KEY,
    SenderUserID    INT  NOT NULL,
    RecipientUserID INT  NOT NULL,
    MessageText     TEXT NOT NULL,
    SentAt          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sender_user FOREIGN KEY (SenderUserID) REFERENCES Users (UserID) ON DELETE CASCADE,
    CONSTRAINT fk_recipient_user FOREIGN KEY (RecipientUserID) REFERENCES Users (UserID) ON DELETE CASCADE
);

-- Create GroupMessages Table
CREATE TABLE IF NOT EXISTS GroupMessages
(
    GroupMessageID SERIAL PRIMARY KEY,
    GroupID        INT  NOT NULL,
    SenderUserID   INT  NOT NULL,
    MessageText    TEXT NOT NULL,
    SentAt         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_message_group FOREIGN KEY (GroupID) REFERENCES Groups (GroupID) ON DELETE CASCADE,
    CONSTRAINT fk_message_sender FOREIGN KEY (SenderUserID) REFERENCES Users (UserID) ON DELETE CASCADE
);


-- Indexes for Users Table
CREATE INDEX IF NOT EXISTS idx_users_username ON Users (Username);
CREATE INDEX IF NOT EXISTS idx_users_email ON Users (Email);

-- Indexes for Friendships Table
CREATE INDEX IF NOT EXISTS idx_friendships_status ON Friendships (Status);
CREATE INDEX IF NOT EXISTS idx_friendships_userid1_userid2 ON Friendships (UserID1, UserID2);

-- Index for Groups Table
CREATE INDEX IF NOT EXISTS idx_groups_groupname ON Groups (GroupName);

-- Index for GroupMembers Table
CREATE INDEX IF NOT EXISTS idx_groupmembers_groupid_userid ON GroupMembers (GroupID, UserID);

-- Indexes for UserMessages Table
CREATE INDEX IF NOT EXISTS idx_usermessages_sender_userid ON UserMessages (SenderUserID);
CREATE INDEX IF NOT EXISTS idx_usermessages_recipient_userid ON UserMessages (RecipientUserID);

-- Indexes for GroupMessages Table
CREATE INDEX IF NOT EXISTS idx_groupmessages_groupid ON GroupMessages (GroupID);
CREATE INDEX IF NOT EXISTS idx_groupmessages_sender_userid ON GroupMessages (SenderUserID);


/