select b.word_vocabulary, b.BE_phonetic_symbol, b.BE_sound, b.is_listening, b.is_speaking, b.is_reading, b.is_writing, b.part_of_speech, b.explanation, p.tinyPic
from (
	select w.word_vocabulary, w.BE_phonetic_symbol, w.BE_sound, w.is_listening, w.is_speaking, w.is_reading, w.is_writing, e.part_of_speech, e.content explanation
	from words w, explanations e
	where w.word_vocabulary = e.word_vocabulary
	and w.word_vocabulary like ?
	and e.category='basic' 
	and e.seq = 1)  b
left outer join pics p
on b.word_vocabulary = p.word_vocabulary
and p.isPrimary = 1
order by %s %s
limit ?
offset ?