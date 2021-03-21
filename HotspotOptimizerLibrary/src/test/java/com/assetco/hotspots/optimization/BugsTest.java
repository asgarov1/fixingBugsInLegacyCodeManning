package com.assetco.hotspots.optimization;

import com.assetco.search.results.Asset;
import com.assetco.search.results.AssetVendor;
import com.assetco.search.results.AssetVendorRelationshipLevel;
import org.junit.jupiter.api.Test;

public class BugsTest {

    @Test
    public void precedingPartnerWithLongTrailingAssetsDoesNotWin() {
        var partnerVendor = makeVendor(Partner);

        var missing = givenAssetInResultsWithVendor(partnerVendor);

        givenAssetInResultsWithVendor(makeVendor(Partner));

        var expected = givenAssetsInResultsWithVendor(maximumShowcaseItems - 1, partnerVendor);

        whenOptimize();

        thenHotspotDoesNotHave(Showcase, missing);
        thenHotspotHasExactly(Showcase, expected);

    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        return null;
    }

    private Asset givenAssetInResultsWithVendor(AssetVendor vendor) {
        return null;
    }

    private Asset getAsset(AssetVendor vendor) {
        return null;
    }

    private AssetPurchaseInfo getPurchaseInfo() {
        return null;
    }

    private void thenHotspotHasExactly(HotspotKey hotspotKey, List<Asset> expected) {
    }
    private ArrayList<Asset> givenAssetsInResultsWithVendor(int count, AssetVendor vendor) {
        var result = new ArrayList<Asset>();
        for (var i = 0; i < count; ++i) {
            result.add(givenAssetInResultsWithVendor(vendor));
        }
        return result;
    }

    private void whenOptimize() {
    }

    private void thenHotspotDoesNotHave(HotspotKey key, Asset... forbidden) {
    }

}
