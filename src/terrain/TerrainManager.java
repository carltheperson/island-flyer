package terrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import entities.Plane;
import rendering.Loader;

public class TerrainManager {

	private static final int CHUNK_SIZE = 60;
	private static final int AMOUNT_OFF_CHUNKS = 13; // At a length, so 3 becomes 3*3 HAS to be uneven

	private HeightsGenerator generator = new HeightsGenerator(0, 0, 128, new Random().nextInt(1000000000));

	private Terrain[][] chunks = new Terrain[AMOUNT_OFF_CHUNKS][AMOUNT_OFF_CHUNKS];
	private ArrayList<Terrain> chunksToBeRendered = new ArrayList<Terrain>();

	private int chunksStartX = 0;
	private int chunksStartZ = 0;

	private Loader loader;
	private Plane player;

	public TerrainManager(Loader loader, Plane player) {
		this.loader = loader;
		this.player = player;
		Terrain.player = player;
		createChunks();
	}

	public void update(Loader loader) {
		renderFromArray();
		updateChunkCoords();

	}

	private void renderFromArray() {
		if (chunksToBeRendered.size() != 0) {
			for (int i = 0; i < chunks.length; i++) {
				for (int j = 0; j < chunks[i].length; j++) {
					chunks[i][j].calculateDistance();

				}
			}
			Collections.sort(chunksToBeRendered, new Comparator<Terrain>() {
				public int compare(Terrain s1, Terrain s2) {
					return (int) (s1.getDistance() - s2.getDistance());
				}

			});
			chunksToBeRendered.get(0).updateChunkData(loader);
			chunksToBeRendered.remove(0);
		}
	}

	private Terrain findCenterChunk() {
		for (int i = 0; i < chunks.length; i++) {
			for (int j = 0; j < chunks[i].length; j++) {
				if (chunks[i][j].getX() == chunksStartX + CHUNK_SIZE * Math.floor(AMOUNT_OFF_CHUNKS / 2)
						&& chunks[i][j].getZ() == chunksStartZ + CHUNK_SIZE * Math.floor(AMOUNT_OFF_CHUNKS / 2)) {
					return chunks[i][j];
				}
			}
		}
		return null;
	}

	private void createChunks() {
		for (int i = 0; i < AMOUNT_OFF_CHUNKS; i++) {
			for (int j = 0; j < AMOUNT_OFF_CHUNKS; j++) {
				chunks[i][j] = new Terrain(i * CHUNK_SIZE, j * CHUNK_SIZE, CHUNK_SIZE, generator);
				chunks[i][j].init(loader);

			}
		}
	}

	private void updateChunkCoords() {
		Terrain centerChunk = findCenterChunk();
		// X +
		if (player.getPosition().x > centerChunk.getX() + CHUNK_SIZE) {
			for (int i = 0; i < chunks.length; i++) {
				for (int j = 0; j < chunks[i].length; j++) {
					if (chunks[i][j].getX() == centerChunk.getX() - CHUNK_SIZE * Math.floor(AMOUNT_OFF_CHUNKS / 2)) {
						chunks[i][j].setX(chunks[i][j].getX() + CHUNK_SIZE * AMOUNT_OFF_CHUNKS);
						chunksToBeRendered.add(chunks[i][j]);
						if (j == chunks[i].length - 1) {
							chunksStartX += CHUNK_SIZE;
						}
					}
				}
			}
		}
		// X -
		if (player.getPosition().x < centerChunk.getX()) {
			for (int i = 0; i < chunks.length; i++) {
				for (int j = 0; j < chunks[i].length; j++) {
					if (chunks[i][j].getX() == centerChunk.getX() + CHUNK_SIZE * Math.floor(AMOUNT_OFF_CHUNKS / 2)) {
						chunks[i][j].setX(chunks[i][j].getX() - CHUNK_SIZE * AMOUNT_OFF_CHUNKS);
						chunksToBeRendered.add(chunks[i][j]);
						if (j == chunks[i].length - 1) {
							chunksStartX -= CHUNK_SIZE;
						}
					}
				}
			}
		}
		// Z +
		if (player.getPosition().z > centerChunk.getZ() + CHUNK_SIZE) {
			for (int i = 0; i < chunks.length; i++) {
				for (int j = 0; j < chunks[i].length; j++) {
					if (chunks[i][j].getZ() == centerChunk.getZ() - CHUNK_SIZE * Math.floor(AMOUNT_OFF_CHUNKS / 2)) {
						chunks[i][j].setZ(chunks[i][j].getZ() + CHUNK_SIZE * AMOUNT_OFF_CHUNKS);
						chunksToBeRendered.add(chunks[i][j]);
						if (i == chunks[i].length - 1) {
							chunksStartZ += CHUNK_SIZE;
						}
					}
				}
			}
		}
		// Z -
		if (player.getPosition().z < centerChunk.getZ()) {
			for (int i = 0; i < chunks.length; i++) {
				for (int j = 0; j < chunks[i].length; j++) {
					if (chunks[i][j].getZ() == centerChunk.getZ() + CHUNK_SIZE * Math.floor(AMOUNT_OFF_CHUNKS / 2)) {
						chunks[i][j].setZ(chunks[i][j].getZ() - CHUNK_SIZE * AMOUNT_OFF_CHUNKS);
						chunksToBeRendered.add(chunks[i][j]);
						if (i == chunks[i].length - 1) {
							chunksStartZ -= CHUNK_SIZE;
						}
					}
				}
			}
		}
	}

	public Terrain[][] getChunks() {
		return chunks;
	}

}
