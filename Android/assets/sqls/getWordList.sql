select b.word_vocabulary, b.BE_phonetic_symbol, b.BE_sound, b.is_listening, b.is_speaking, b.is_reading, b.is_writing, b.part_of_speech, b.explanation, b.tinyPic
from words b
where  b.word_vocabulary like ?
order by %s %s
limit ?
offset ?