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

    private final int maximumShowcaseItems = 5;
    private SearchResults searchResults;
    private AssetVendor partnerVendor;
    private SearchResultHotspotOptimizer optimizer;

    @Test
    public void prevailingPartnerReceivesFirstFiveItemsInShowcase() {
        // now it's no longer "missing"
        ArrayList<Asset> expected = new ArrayList<>();
        // so we include it in the list of expected items
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        givenAssetInResultsWithVendor(makeVendor(Partner));
        expected.addAll(givenAssetsInResultsWithVendor(maximumShowcaseItems - 1, partnerVendor));

        whenOptimize();

        // check that everything is there
        thenHotspotHasExactly(Showcase, expected);
    }

    @BeforeEach
    public void setUp() {
        optimizer = new SearchResultHotspotOptimizer();
        searchResults = new SearchResults();
        partnerVendor = makeVendor(Partner);
    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        return new AssetVendor("anything", "anything", relationshipLevel, 1);
    }

    private AssetPurchaseInfo getPurchaseInfoFromRoyaltiesAndRevenue(String royalties, String revenue) {
        return new AssetPurchaseInfo(0, 0,
                new Money(new BigDecimal(revenue)),
                new Money(new BigDecimal(royalties)));
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
        return getPurchaseInfoFromRoyaltiesAndRevenue("0", "0");
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
        optimizer.optimize(searchResults);
    }

    private void thenHotspotDoesNotHave(HotspotKey key, Asset... forbidden) {
        for (var asset : forbidden)
            assertFalse(searchResults.getHotspot(key).getMembers().contains(asset));
    }
}