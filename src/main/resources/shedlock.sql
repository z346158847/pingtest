CREATE TABLE shedlock(
    name VARCHAR(64),
    lock_until TIMESTAMP(3) NULL,
    locked_at TIMESTAMP(3) NULL,
    locked_by  VARCHAR(255),
    PRIMARY KEY (name)
)
-- name是全局唯一的。用这个来标识全局唯一的定时任务。用此来变相实现一个悲观锁。