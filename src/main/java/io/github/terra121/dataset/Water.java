package io.github.terra121.dataset;

import io.github.terra121.dataset.osm.OpenStreetMap;
import io.github.terra121.dataset.osm.OSMRegion;
import net.minecraft.util.math.ChunkPos;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

public class Water {
    public WaterGround grounding;
    public OpenStreetMap osm;
    public int hres;

    public HashSet<ChunkPos> inverts;
    public boolean doingInverts;

    public Water(OpenStreetMap osm, int horizontalres) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("assets/terra121/data/ground.dat");
        this.grounding = new WaterGround(is);
        this.osm = osm;
        this.hres = horizontalres;
        this.inverts = new HashSet<>();
        this.doingInverts = false;
    }

    public byte getState(double lon, double lat) {
        OSMRegion region = this.osm.regionCache(new double[]{ lon, lat });

        //default if download failed
        if (region == null) {
            return 0;
        }

        //transform to water render res
        lon -= region.west;
        lat -= region.south;
        lon /= OpenStreetMap.TILE_SIZE / this.hres;
        lat /= OpenStreetMap.TILE_SIZE / this.hres;

        //TODO: range check
        int idx = region.getStateIdx((short) lon, (short) lat);

        byte state = region.states[(int) lon][idx];

        if (this.doingInverts && (state == 0 || state == 1) && this.inverts.contains(region.coord)) {
            state = state == 1 ? (byte) 0 : (byte) 1; //invert state if in an inverted region
        }

        return state;
    }

    //TODO: more efficient
    public float estimateLocal(double lon, double lat) {
        //bound check
        if (!(lon <= 180 && lon >= -180 && lat <= 80 && lat >= -80)) {
            if (lat < -80) //antartica is land
            {
                return 0;
            }
            return 2; //all other out of bounds is water
        }

        double oshift = OpenStreetMap.TILE_SIZE / this.hres;
        double ashift = OpenStreetMap.TILE_SIZE / this.hres;

        //rounding errors fixed by recalculating values from scratch (wonder if this glitch also causes the oddly strait terrain that sometimes appears)
        double Ob = Math.floor(lon / oshift) * oshift;
        double Ab = Math.floor(lat / ashift) * ashift;

        double Ot = Math.ceil(lon / oshift) * oshift;
        double At = Math.ceil(lat / ashift) * ashift;

        float u = (float) ((lon - Ob) / oshift);
        float v = (float) ((lat - Ab) / ashift);

        float ll = this.getState(Ob, Ab);
        float lr = this.getState(Ot, Ab);
        float ur = this.getState(Ot, At);
        float ul = this.getState(Ob, At);

        //all is ocean
        if (ll == 2 || lr == 2 || ur == 2 || ul == 2) {
            if (ll < 2) {
                ll += 1;
            }
            if (lr < 2) {
                lr += 1;
            }
            if (ur < 2) {
                ur += 1;
            }
            if (ul < 2) {
                ul += 1;
            }
        }

        //get perlin style interpolation on this block
        return (1 - v) * (ll * (1 - u) + lr * u) + (ul * (1 - u) + ur * u) * v;
    }
}
