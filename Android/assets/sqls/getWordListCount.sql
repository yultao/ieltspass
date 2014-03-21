select count(*) from words w LEFT JOIN familiarity f
ON w.word_Id = f.word_Id
where w.word_vocabulary like ?
%s