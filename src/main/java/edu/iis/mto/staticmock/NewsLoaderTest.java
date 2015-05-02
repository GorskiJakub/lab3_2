package edu.iis.mto.staticmock;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.iis.mto.staticmock.Configuration;
import edu.iis.mto.staticmock.ConfigurationLoader;
import edu.iis.mto.staticmock.IncomingInfo;
import edu.iis.mto.staticmock.IncomingNews;
import edu.iis.mto.staticmock.NewsLoader;
import edu.iis.mto.staticmock.NewsReaderFactory;
import edu.iis.mto.staticmock.SubsciptionType;
import edu.iis.mto.staticmock.reader.NewsReader;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigurationLoader.class, NewsReaderFactory.class })
public class NewsLoaderTest {

	NewsLoader newsLoader;
	IncomingNews incomingNews;

	ConfigurationLoader configurationLoader;
	NewsReaderFactory newsReaderFactory;
	Configuration config;
	NewsReader reader;

	public void Config() throws Exception {

		mockStatic(ConfigurationLoader.class);
		mockStatic(NewsReaderFactory.class);

		newsLoader = new NewsLoader();
		incomingNews = new IncomingNews();
		incomingNews.add(mockIncomingInfo(SubsciptionType.A));
		incomingNews.add(mockIncomingInfo(SubsciptionType.A));
		incomingNews.add(mockIncomingInfo(SubsciptionType.B));
		incomingNews.add(mockIncomingInfo(SubsciptionType.NONE));

		configurationLoader = mock(ConfigurationLoader.class);
		newsReaderFactory = mock(NewsReaderFactory.class);
		config = mock(Configuration.class);
		reader = mock(NewsReader.class);

		when(ConfigurationLoader.getInstance()).thenReturn(configurationLoader);
		when(ConfigurationLoader.loadConfiguration()).thenReturn(config);

		when(NewsReaderFactory.getReader(Mockito.anyString())).thenReturn(
				reader);
		when(reader.read()).thenReturn(incomingNews);
	}

	@Test
	public void loadNews_expectedNoneSubscribentContent_and_OnePublicContent() {
		PublishableNews publishableNews = newsLoader.loadNews();
		assertThat(publishableNews.getPublicContent().size(), is(1));
		assertThat(publishableNews.getSubscribentContent().size(), is(3));
	}

	@Test
	public void behaviorTest_getReaderStarts_exptectedOne () throws Exception {
		PublishableNews publishableNews = newsLoader.loadNews();
		PowerMockito.verifyStatic(Mockito.times(1));
		NewsReaderFactory.getReader(Mockito.anyString());
	}

	private IncomingInfo mockIncomingInfo(SubsciptionType type) {

		IncomingInfo incomingInfo = mock(IncomingInfo.class);
		when(incomingInfo.requiresSubsciption()).thenReturn(
				type != SubsciptionType.NONE);
		when(incomingInfo.getSubscriptionType()).thenReturn(type);

		return incomingInfo;
	}

}
