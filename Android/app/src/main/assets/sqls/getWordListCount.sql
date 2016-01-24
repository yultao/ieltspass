select count(*) from words w LEFT JOIN familiarity f
ON w.word_id = f.word_id
where w.word_vocabulary like ?
%s