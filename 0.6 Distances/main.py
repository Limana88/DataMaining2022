import nltk
import numpy as numpy
from pymorphy2 import MorphAnalyzer
from numpy import sqrt

def jaccard_coefficient(set_one, set_two):
    intersection = len(set.intersection(set_one, set_two))
    union = len(set.union(set_one, set_two))
    return intersection/union

def cosine_similarity(list_one, list_two):
    all_words = list(set(list_one + list_two))

    vector_one = numpy.zeros(len(all_words))
    vector_two = numpy.zeros(len(all_words))

    for i in range(len(all_words)):
        vector_one[i] = list_one.count(all_words[i])
        vector_two[i] = list_two.count(all_words[i])

    numerator = sum([a * b for a, b in zip(vector_one,vector_two)])
    denominator = sqrt(sum(a * a for a in vector_one)) * sqrt(sum(b * b for b in vector_two))
    return numerator/denominator

if __name__ == '__main__':
    russian_stopwords = nltk.corpus.stopwords.words('russian')
    analyzer = MorphAnalyzer()

    article_file = open('article.txt')
    list_words = []
    punctuations_mark = [':', '.', ',', '?', '—', '%']

    for item in article_file:
        list_words = [i for i in item.split() if not i.isdigit()]

    for index, item in enumerate(list_words):
        item = item.lower()

        for mark in punctuations_mark:
            item = item.replace(mark, '')
        word = analyzer.parse(item)

        list_words[index] = word[0].normal_form


    filtered_list_words = [word for word in list_words if word not in russian_stopwords]
    print(len(filtered_list_words))
    science_words_file = open('science.txt')
    science_words = []
    for line in science_words_file:
        line = line.replace('\n', '')
        science_words.append(line)

    news_words_file = open('news.txt')
    news_words = []
    for line in news_words_file:
        line = line.replace('\n', '')
        news_words.append(line)

    shopping_words_file = open('shopping.txt')
    shopping_words = []
    for line in shopping_words_file:
        line = line.replace('\n', '')
        shopping_words.append(line)

    sport_words_file = open('sport.txt')
    sport_words = []
    for line in sport_words_file:
        line = line.replace('\n', '')
        sport_words.append(line)

    print(jaccard_coefficient(set(filtered_list_words), set(science_words)))
    print(jaccard_coefficient(set(filtered_list_words), set(shopping_words)))
    print(jaccard_coefficient(set(filtered_list_words), set(news_words)))
    print(jaccard_coefficient(set(filtered_list_words), set(sport_words)))

    print()
    print(cosine_similarity(filtered_list_words, science_words))
    print(cosine_similarity(filtered_list_words, shopping_words))
    print(cosine_similarity(filtered_list_words, news_words))
    print(cosine_similarity(filtered_list_words, sport_words))


