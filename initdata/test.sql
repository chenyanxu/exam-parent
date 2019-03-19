--select a.userid,b.name,a.teachertype from exam_teacher a, sys_user b where  b.id=a.userid;

--select a.examanswerid,a.userid,a.examid,a.teacherid,b.name,a.score,c.itemdeductscore from exam_score a,sys_user b,exam_score_item c where a.teacherId = b.id and a.id=c.examscoreid and a.teacherid=1004511;
--select a.examanswerid,a.userid,a.examid,a.teacherid,b.name,a.score,c.itemdeductscore from exam_score a,sys_user b,exam_score_item c where a.teacherId = b.id and a.id=c.examscoreid and a.teacherid=1004512;

select * from exam_answer where id=1005615;
select a.totalscore from exam_examinee a where a.userid=23 and a.examid=1005425;

--select a.examanswerid,a.userid,a.examid,a.teacherid,b.name,a.score,c.itemdeductscore from exam_score a,sys_user b,exam_score_item c
--where a.teacherId = b.id and a.id=c.examscoreid and a.teacherid=1004516 and a.examanswerid=1005615;
