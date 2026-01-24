package deus.btd.pipeline.io;

import com.b100.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import deus.btd.Main;
import deus.btd.mixin.TexturePackCustomAccessor;
import deus.btd.pipeline.compile.types.AtomType;
import deus.btd.pipeline.project.ProjectResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePackCustom;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.data.registry.Registries;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static deus.btd.Main.LOGGER;

public class AtomProjectLoader {
	public static List<ProjectResources> loadProjects(Path folder) {
		List<ProjectResources> projectResources = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(folder)) {
			paths
				.filter(Files::isRegularFile)
				.filter(p -> p.getFileName().toString().endsWith(AtomType.PROJECT.suffix()))
				.forEach(zip -> {
					try {
						projectResources.add(new ProjectResources(zip));
					} catch (Exception e) {
						LOGGER.error("Failed to load atom file: {}", zip, e);
					}
				});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return projectResources;
	}
	public static void loadDatapack(String name, ZipResources resources) {
		LOGGER.info("Loading data from {}", name);

		Path manifestPath = resources.get("recipes/manifest.json");
		if (!Files.exists(manifestPath)) {
			LOGGER.warn("manifest.json not found in {}", name);
			return;
		}

		try (InputStream is = Files.newInputStream(manifestPath);
			 InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {

			Gson gson = new Gson();
			TypeToken<Map<String, List<String>>> token = new TypeToken<Map<String, List<String>>>() {};
			Map<String, List<String>> map = gson.fromJson(reader, token);

			List<String> recipeFiles = map.get("added_recipes");
			List<String> groupFiles = map.get("added_item_groups");
			List<String> removedRecipes = map.get("removed_recipes");

			// --- Remove recipes ---
			if (removedRecipes != null && !removedRecipes.isEmpty()) {
				for (String removedRecipe : removedRecipes) {
					String[] deconstructedKey = Registries.RECIPES.deconstructKey(removedRecipe);
					Registries.RECIPES.getGroupFromKey(removedRecipe).unregister(deconstructedKey[2]);
				}
				LOGGER.info("Removed {} recipes", removedRecipes.size());
			}

			// --- Load item groups ---
			if (groupFiles != null && !groupFiles.isEmpty()) {
				for (String groupFile : groupFiles) {
					Path groupFilePath = resources.get("recipes/" + groupFile);
					if (Files.exists(groupFilePath)) {
						try (InputStream groupIs = Files.newInputStream(groupFilePath)) {
							String contents = StringUtils.readInputString(groupIs);
							LOGGER.info("Loading item groups from {}/{}", name, groupFile);
							DataLoader.loadItemGroupsFromString(contents);
						}
					}
				}
			}

			// --- Load recipes ---
			if (recipeFiles != null && !recipeFiles.isEmpty()) {
				for (String recipeFile : recipeFiles) {
					Path recipePath = resources.get("recipes/ " + recipeFile);
					if (Files.exists(recipePath)) {
						try (InputStream recipeIs = Files.newInputStream(recipePath)) {
							String contents = StringUtils.readInputString(recipeIs);
							LOGGER.info("Loading recipes from {}/{}", name, recipeFile);
							DataLoader.loadRecipesFromString(contents);
						}
					}
				}
			}

		} catch (IOException e) {
			LOGGER.error("Failed to load data from {}", name, e);
		}
	}

	public static Map<AtomType, List<?>> loadAtomsFromProject(ProjectResources project) {
		Map<AtomType, List<?>> atoms = new EnumMap<>(AtomType.class);

		for (AtomType type : AtomType.values()) {
			atoms.put(type, new ArrayList<>());
		}

		ZipResources zip = project.zip();

		try (Stream<Path> paths = Files.walk(zip.get("/data"))) {
			paths
				.filter(Files::isRegularFile)
				.filter(p -> p.toString().endsWith(".atom"))
				.forEach(p -> {
					try {
						AtomTomlLoader.processAtomFile(
							project.name(),
							p,
							atoms
						);
					} catch (Exception e) {
						Main.LOGGER.error("Failed loading atom for {}", p.toString());
					}
				});
		}  catch (Exception e) {
			Main.LOGGER.error("Failed loading atoms for {}", project.name(), e);
		}

		return atoms;
	}

	public static void loadTextures(ProjectResources project) {
		if (!project.hasAssets()) return;

		try {
			File tempZip = File.createTempFile(project.name(), ".zip");
			tempZip.deleteOnExit();

			try (ZipFile originalZip = new ZipFile(project.originalZipPath.toFile());
				 ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(tempZip.toPath()))) {

				Enumeration<? extends ZipEntry> entries = originalZip.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();

					if (entry.getName().startsWith("assets/")) {
						ZipEntry newEntry = new ZipEntry(entry.getName());
						zos.putNextEntry(newEntry);

						try (InputStream is = originalZip.getInputStream(entry)) {
							byte[] buffer = new byte[4096];
							int len;
							while ((len = is.read(buffer)) > 0) {
								zos.write(buffer, 0, len);
							}
						}

						zos.closeEntry();
					}
				}
			}

			TexturePackCustom texturePackCustom = new TexturePackCustom(tempZip);

			if (project.zip().exists("assets/pack.png")) {
				((TexturePackCustomAccessor)texturePackCustom).setThumbnailBuffer(project.zip().readImageSafe("assets/pack.png"));
			}

			texturePackCustom.readZipFile();
			texturePackCustom.readTexturePackManifest();
			LOGGER.info("Loaded texturepack from atompack successfully");

			Minecraft.getMinecraft().texturePackList.selectedPacks.add(texturePackCustom);

		} catch (IOException e) {
			LOGGER.error("Failed to load texture pack", e);
		}
	}
}
