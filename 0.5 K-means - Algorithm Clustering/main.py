import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
plt.style.use("seaborn")
from mpl_toolkits.mplot3d import Axes3D

from sklearn.cluster import KMeans
from sklearn.preprocessing import MinMaxScaler

if __name__ == '__main__':
    pd.set_option('display.max_rows', None)
    pd.set_option('display.max_columns', None)
    pd.set_option('display.max_colwidth', None)

    df1 = pd.read_csv('df1.csv')
    df2 = pd.read_csv('df2.csv')
    df = pd.concat([df1, df2], 0)
    df = df[['name', 'album', 'artist', 'id', 'release_date', 'popularity', 'danceability', 'energy', 'valence',
             'loudness']]

    df.to_csv("temp_df.csv", index=False)

    fig, axes = plt.subplots(2, 2, figsize=(15, 8))

    axes[0, 0].hist(df['danceability'])
    axes[0, 0].set_title('Danceability', fontsize=15)
    axes[0, 1].hist(df['energy'])
    axes[0, 1].set_title('Energy', fontsize=15)
    axes[1, 0].hist(df['valence'])
    axes[1, 0].set_title('Valence', fontsize=15)
    axes[1, 1].hist(df['loudness'])
    axes[1, 1].set_title('Loudness', fontsize=15)
    # plt.show()

    df.describe()

    col_features = df.columns[6:]
    X = MinMaxScaler().fit_transform(df[col_features])

    kmeans = KMeans(init="k-means++", n_clusters=2, random_state=15).fit(X)

    df['kmeans'] = kmeans.labels_

    fig = plt.figure(figsize=(10, 8))
    ax = fig.add_subplot(111, projection='3d')

    x = df['energy']
    y = df['danceability']
    z = df['loudness']
    cmhot = cmhot = plt.get_cmap('bwr')

    ax.scatter(x, y, z, c=df['kmeans'], s=40, cmap=cmhot)
    ax.set_xlabel('Energy', fontsize=12)
    ax.set_ylabel('Danceability', fontsize=12)
    ax.set_zlabel('Loudness', fontsize=12)
    ax.set_title("3D Scatter Plot of Songs Clustered")

    plt.savefig("3d plot.png")

    df.groupby(['kmeans']).mean()


    df.to_csv("df_with_kmeans.csv")

    cluster_0 = df[df['kmeans'] == 0]
    cluster_1 = df[df['kmeans'] == 1]

    cluster_0.to_csv("cluster0.csv", index=False)
    cluster_1.to_csv("cluster1.csv", index=False)
    df.to_csv("df.csv", index=False)