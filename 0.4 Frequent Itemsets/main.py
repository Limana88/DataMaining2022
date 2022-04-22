# This is a sample Python script.

# Press ⌃R to execute it or replace it with your code.
# Press Double ⇧ to search everywhere for classes, files, tool windows, actions, and settings.
import pandas as pd
import numpy as np

from itertools import combinations


def make_mapping(data):
    return dict([(v, k) for k, v in enumerate(data)])


def make_inv_mapping(data_mapping):
    return dict([(v, k) for k, v in data_mapping.items()])


if __name__ == '__main__':
    support_s = 4
    data = pd.read_csv('transaction.csv', header=0, names=['PROD_CODE', 'BASKET_ID'])

    items = sorted(data['PROD_CODE'].unique())
    member_ids = sorted(data['BASKET_ID'].unique())
    len(items), len(member_ids)

    items_mapping = make_mapping(items)
    items_inv_mapping = make_inv_mapping(items_mapping)

    print(items_mapping)

    data['Item_mapping'] = data['PROD_CODE'].apply(lambda item: items_mapping[item])
    data.head()

    # print(data)

    transactions = data.groupby('BASKET_ID')['Item_mapping'].apply(list).to_frame()

    # print(data)

    item_counts = data['Item_mapping'].value_counts()


    singletons = item_counts[item_counts >= support_s]
    unsupporteds = item_counts[item_counts < support_s]


    doubletons = data.groupby('BASKET_ID')['Item_mapping'] \
        .apply(lambda items: list(combinations(items, 2))) \
        .to_frame(name='Item_combinations')

    k = len(items)


def hash_pair1(pair, k=len(items)):
    return (pair[0] + pair[1]) % k


def hash_pair2(pair, k=len(items)):
    return (pair[0] + 2 * pair[1]) % k


all_pairs = sum(([x for x in doubletons['Item_combinations']]), [])
len(all_pairs)


hash_buckets1 = [[] for _ in range(k)]

for pair in all_pairs:
    hash_buckets1[hash_pair1(pair)].append(pair)

pd.Series(hash_buckets1).to_frame('Hash_bucket')


unsupported_pairs1 = []

for group in hash_buckets1:
    if len(group) < support_s:
        for p in group:
            unsupported_pairs1.append(p)


hash_buckets2 = [[] for _ in range(k)]

for pair in all_pairs:
    hash_buckets2[hash_pair2(pair)].append(pair)

pd.Series(hash_buckets2).to_frame('Hash_bucket')



for i in range(len(hash_buckets2)):
    for p in unsupported_pairs1:
        if p in hash_buckets2[i]:
            hash_buckets2[i].remove(p)

for group in hash_buckets2:
    if len(group) < support_s:
        hash_buckets2.remove(group)


frequent_doubletons = []
for group in hash_buckets2:
    for p in group:
        # print(p)
        if((p[0] - 1 != -1) and (p[1] - 1 != -1)):
            item1 = items_inv_mapping[p[0] - 1]
            item2 = items_inv_mapping[p[1] - 1]

            if item1 not in unsupporteds and item2 not in unsupporteds and item1 != item2:
                frequent_doubletons.append((items_mapping[item1], items_mapping[item2]))

var = [(items_inv_mapping[pair[0]], items_inv_mapping[pair[1]])
 for pair in set(frequent_doubletons)]

for index in singletons.index:
    print(items_inv_mapping[index])
print(var)


