create table words(BE_phonetic_symbol text,AE_phonetic_symbol text,BE_sound text,AE_sound text,is_listening int,is_speaking int,is_reading int,is_writing int,word_vocabulary text);
create table examples(sentence text,cn_explanation text,sentence_sound text,word_vocabulary text);
create table pics(tinyPic text,normalPic text,isPrimary int,word_vocabulary text);
create table memory_methods(memoryWay text,isPrimary int,word_vocabulary text);
create table explanations(seq int,content text, part_of_speech text,category text,word_vocabulary text);