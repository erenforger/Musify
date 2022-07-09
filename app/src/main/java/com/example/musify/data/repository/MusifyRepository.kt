package com.example.musify.data.repository

import com.example.musify.data.dto.*
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.token.BearerToken
import com.example.musify.data.repository.tokenrepository.TokenRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusicSummary
import com.example.musify.domain.MusifyHttpErrorType
import com.example.musify.domain.SearchResult
import com.example.musify.domain.musifyHttpErrorType
import retrofit2.HttpException
import javax.inject.Inject

/**
 * A concrete implementation of [Repository].
 */
class MusifyRepository @Inject constructor(
    private val spotifyService: SpotifyService,
    private val tokenRepository: TokenRepository
) : Repository {
    private suspend fun <R> withToken(block: suspend (BearerToken) -> R): FetchedResource<R, MusifyHttpErrorType> =
        try {
            FetchedResource.Success(block(tokenRepository.getValidBearerToken()))
        } catch (httpException: HttpException) {
            FetchedResource.Failure(httpException.musifyHttpErrorType)
        }

    override suspend fun fetchArtistSummaryForId(
        artistId: String,
        imageSize: MapperImageSize
    ): FetchedResource<MusicSummary.ArtistSummary, MusifyHttpErrorType> = withToken {
        spotifyService.getArtistInfoWithId(artistId, it).toArtistSummary(imageSize)
    }

    override suspend fun fetchAlbumsOfArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String, //ISO 3166-1 alpha-2 country code
    ): FetchedResource<List<MusicSummary.AlbumSummary>, MusifyHttpErrorType> = withToken {
        spotifyService.getAlbumsOfArtistWithId(
            artistId,
            countryCode,
            it
        ).toAlbumSummaryList(imageSize)
    }

    override suspend fun fetchTopTenTracksForArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<MusicSummary.TrackSummary>, MusifyHttpErrorType> = withToken {
        spotifyService.getTopTenTracksForArtistWithId(
            artistId = artistId,
            market = countryCode,
            token = it,
        ).value.map { trackDTOWithAlbumMetadata ->
            trackDTOWithAlbumMetadata.toTrackSummary(imageSize)
        }
    }

    override suspend fun fetchAlbumWithId(
        albumId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<MusicSummary.AlbumSummary, MusifyHttpErrorType> = withToken {
        spotifyService.getAlbumWithId(albumId, countryCode, it).toAlbumSummary(imageSize)
    }

    override suspend fun fetchPlaylistWithId(
        playlistId: String,
        countryCode: String
    ): FetchedResource<MusicSummary.PlaylistSummary, MusifyHttpErrorType> = withToken {
        spotifyService.getPlaylistWithId(playlistId, countryCode, it).toPlayListSummary()
    }

    override suspend fun fetchSearchResultsForQuery(
        searchQuery: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<SearchResult, MusifyHttpErrorType> = withToken {
        spotifyService.search(searchQuery, countryCode, it).toSearchResult(imageSize)
    }
}