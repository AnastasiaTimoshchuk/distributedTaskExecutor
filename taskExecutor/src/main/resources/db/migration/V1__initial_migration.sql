create schema if not exists task_executor;

create table if not exists task_executor.tasks
(
    id                          serial primary key,
    name                        varchar(255) not null,
    message_id                  varchar(255) not null,
    duration                    int not null,
    status                      varchar(255),
    create_date                 timestamp,
    update_date                 timestamp,
    expected_execution_date     timestamp,
    execution_tries             int
);

create index if not exists idx_tasks_status on task_executor.tasks (status);
create unique index if not exists idx_tasks_unique_message_id on task_executor.tasks (message_id);