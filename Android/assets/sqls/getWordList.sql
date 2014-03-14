select w.word_vocabulary, w.BE_phonetic_symbol, w.BE_sound, w.is_listening, w.is_speaking, w.is_reading, w.is_writing, w.part_of_speech, w.explanation, w.tinyPic,f.familiarity_class
from words w
LEFT JOIN familiarity f
ON w.word_vocabulary = f.word_vocabulary
where  w.word_vocabulary like ?
%s
order by %s %s
limit ?
offset ?