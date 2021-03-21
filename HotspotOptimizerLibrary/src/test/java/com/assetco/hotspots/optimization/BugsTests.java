package com.assetco.hotspots.optimization;

import com.assetco.search.results.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.assetco.search.results.AssetVendorRelationshipLevel.Partner;
import static com.assetco.search.results.HotspotKey.Showcase;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BugsTests {

    SearchResults searchResults;

    @BeforeEach
    public void init() {
        searchResults = new SearchResults();
    }

    @Test
    public void precedingPartnerWithLongTrailingAssetsDoesNotWin() {
        final var partnerVendor = makeVendor(Partner);
        final int maximumShowcaseItems = 5;
        // I don't need to track this, yet, but I like my tests to be very explanatory
        var missing =
                // We'll need an asset from a different partner-level vendor
                givenAssetInResultsWithVendor(partnerVendor);
        // This is the "salt" that makes the system work differently from how business expected
        givenAssetInResultsWithVendor(makeVendor(Partner));
        // This is what is actually put in the showcase box
        var expected = givenAssetsInResultsWithVendor(maximumShowcaseItems - 1, partnerVendor);

        whenOptimize();

        thenHotspotDoesNotHave(Showcase, missing);
        thenHotspotHasExactly(Showcase, expected);
    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        return new AssetVendor("test_id", "test_name", relationshipLevel, 0f);
    }

    private Asset givenAssetInResultsWithVendor(AssetVendor vendor) {
        Asset result = getAsset(vendor);
        searchResults.addFound(result);
        return result;
    }

    private Asset getAsset(AssetVendor vendor) {
        return new Asset("anything", "anything", null, null, getPurchaseInfo(), getPurchaseInfo(), new ArrayList<>(), vendor);
    }

    private AssetPurchaseInfo getPurchaseInfo() {
        return new AssetPurchaseInfo(0, 0,
                new Money(BigDecimal.valueOf(0)),
                new Money(BigDecimal.valueOf(0)));
    }

    private void thenHotspotHasExactly(HotspotKey hotspotKey, List<Asset> expected) {
        Assertions.assertArrayEquals(expected.toArray(), searchResults.getHotspot(hotspotKey).getMembers().toArray());
    }

    private ArrayList<Asset> givenAssetsInResultsWithVendor(int count, AssetVendor vendor) {
        var result = new ArrayList<Asset>();
        for (var i = 0; i < count; ++i) {
            result.add(givenAssetInResultsWithVendor(vendor));
        }
        return result;
    }

    private void whenOptimize() {
        var optimizer = new SearchResultHotspotOptimizer();
        optimizer.optimize(searchResults);
    }

    private void thenHotspotDoesNotHave(HotspotKey key, Asset... forbidden) {
        for (var asset : forbidden)
            assertFalse(searchResults.getHotspot(key).getMembers().contains(asset));
    }
}