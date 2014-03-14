select count(*) from words w LEFT JOIN familiarity f
ON w.word_vocabulary = f.word_vocabulary
where w.word_vocabulary like ?
%s