alter table town
    add constraint FK_country_id foreign key (country_id)
        references country (id);

alter table "user"
    add constraint FK_town_id foreign key (town_id)
        references town (id);

alter table friendship
    add constraint FK_src_user_id foreign key (src_user_id)
        references "user" (id);

alter table friendship
    add constraint FK_dst_user_id foreign key (dst_user_id)
        references "user" (id);

alter table message
    add constraint FK_author_id foreign key (author_id)
        references "user" (id);

alter table post
    add constraint FK_post_author_id foreign key (author_id)
        references "user" (id);

alter table file_info
    add constraint FK_owner_id foreign key (owner_id)
        references "user" (id);

alter table post2tag
    add constraint FK_post_id foreign key (post_id)
        references post (id);

alter table post2tag
    add constraint FK_tag_id foreign key (tag_id)
        references tag (id);

alter table post_like
    add constraint FK_post_like_id foreign key (post_id)
        references post (id);

alter table post_like
    add constraint FK_post_user_id foreign key (user_id)
        references "user" (id);

alter table post_file
    add constraint FK_post_file_id foreign key (post_id)
        references post (id);

alter table post_comment
    add constraint FK_post_comment_id foreign key (post_id)
        references post (id);

alter table post_comment
    add constraint FK_post_parant_id foreign key (parent_id)
        references post_comment (id);

alter table post_comment
    add constraint FK_post_author_id foreign key (author_id)
        references "user" (id);

alter table report
    add constraint FK_post_report_id foreign key (post_id)
        references post (id);

alter table report
    add constraint FK_comment_id foreign key (comment_id)
        references post_comment (id);

alter table report
    add constraint FK_report_author_id foreign key (author_id)
        references "user" (id);

alter table notification
    add constraint FK_notification_user_id foreign key (user_id)
        references "user" (id);

alter table block_history
    add constraint FK_bh_user_id foreign key (user_id)
        references "user" (id);

alter table block_history
    add constraint FK_bh_post_id foreign key (post_id)
        references post (id);

alter table block_history
    add constraint FK_bh_comment_id foreign key (comment_id)
        references post_comment (id);

alter table message
    add constraint FK_dialog_id foreign key (dialog_id)
        references dialog (id);

alter table dialog
    add constraint FK_dialog_owner_id foreign key (owner_id)
        references "user" (id);

alter table dialog
    add constraint FK_dialog_recipient_id foreign key (recipient_id)
        references "user" (id);